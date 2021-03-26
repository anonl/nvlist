package nl.weeaboo.vn.buildtools.optimizer.sound;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.io.FileUtil;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.file.OptimizerFileUtil;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerContext;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerFileSet;
import nl.weeaboo.vn.buildtools.optimizer.IParallelExecutor;
import nl.weeaboo.vn.buildtools.optimizer.MainOptimizerConfig;
import nl.weeaboo.vn.buildtools.optimizer.sound.encoder.FfmpegSoundEncoder;
import nl.weeaboo.vn.buildtools.optimizer.sound.encoder.ISoundEncoder;
import nl.weeaboo.vn.buildtools.optimizer.sound.encoder.NoOpSoundEncoder;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.sound.SoundModule;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinition;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionCache;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionIO;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

/**
 * Audio file optimizer.
 */
public final class SoundOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(SoundOptimizer.class);

    private final IParallelExecutor executor;
    private final MainOptimizerConfig optimizerConfig;
    private final IOptimizerFileSet optimizerFileSet;
    private final ITempFileProvider tempFileProvider;
    private final IFileSystem resFileSystem;
    private final SoundDefinitionCache soundDefCache;

    // --- State during optimization ---
    /** Definition per (optimized) sound file */
    private final Map<FilePath, SoundDefinition> optimizedDefs = Maps.newHashMap();
    private EAvailable ffmpegAvailable;

    public SoundOptimizer(IOptimizerContext context) {
        executor = context.getExecutor();
        optimizerConfig = context.getMainConfig();
        optimizerFileSet = context.getFileSet();
        tempFileProvider = context.getTempFileProvider();

        NvlistProjectConnection project = context.getProject();
        resFileSystem = project.getResFileSystem();
        soundDefCache = new SoundDefinitionCache(resFileSystem);

        resetState();
    }

    private void resetState() {
        optimizedDefs.clear();

        ffmpegAvailable = EAvailable.UNKNOWN;
    }

    /**
     * Runs the optimizer.
     *
     * @throws InterruptedException If the current thread is interrupted before the optimization is finished.
     */
    public void optimizeResources() throws InterruptedException {
        resetState();

        optimizeSounds();
        writeSoundDefinitions();
    }

    private void optimizeSounds() throws InterruptedException {
        ImmutableList<FilePath> inputFiles = ImmutableList.copyOf(getSoundFiles());
        executor.invokeAndWait(inputFiles, inputFile -> {
            try {
                optimizeSound(inputFile);
            } catch (IOException | RuntimeException e) {
                LOG.warn("Error optimizing audio file: {}", inputFile, e);
            }
        });
    }

    private void writeSoundDefinitions() {
        Multimap<FilePath, SoundDefinition> defsPerFolder = HashMultimap.create();
        for (Map.Entry<FilePath, SoundDefinition> entry : optimizedDefs.entrySet()) {
            defsPerFolder.put(entry.getKey().getParent(), entry.getValue());
        }

        for (Map.Entry<FilePath, Collection<SoundDefinition>> folderEntry : defsPerFolder.asMap().entrySet()) {
            FilePath jsonRelativePath = folderEntry.getKey().resolve(ISoundDefinition.SND_DEF_FILE);
            optimizerFileSet.markOptimized(jsonRelativePath);

            File outputF = new File(optimizerConfig.getOutputFolder(), jsonRelativePath.toString());

            String serialized = SoundDefinitionIO.serialize(folderEntry.getValue());
            try {
                FileUtil.writeUtf8(outputF, serialized);
            } catch (IOException e) {
                LOG.warn("Error writing {}: {}", jsonRelativePath, e);
            }
        }
    }

    private Iterable<FilePath> getSoundFiles() {
        FileCollectOptions filter = FileCollectOptions.files(MediaType.SOUND.getSubFolder());
        return OptimizerFileUtil.filterByExts(resFileSystem.getFiles(filter), SoundModule.getSupportedFileExts());
    }

    private void optimizeSound(FilePath inputFile) throws IOException {
        if (!optimizerFileSet.requiresOptimize(inputFile)) {
            LOG.debug("Skip sound: {}", inputFile);
            return;
        }

        LOG.debug("Optimizing sound: {}", inputFile);

        SoundWithDef soundWithDef = loadInput(inputFile);

        ISoundEncoder encoder = createEncoder();
        EncodedSound encoded = encoder.encode(soundWithDef);

        FilePath outputPath = getOutputPath(inputFile, encoded.getDef().getFilename());

        // Write optimized file
        File outputF = new File(optimizerConfig.getOutputFolder(), outputPath.toString());
        Files.createParentDirs(outputF);
        Files.write(encoded.readBytes(), outputF);

        optimizedDefs.put(outputPath, encoded.getDef());
        optimizerFileSet.markOptimized(inputFile);

        soundWithDef.dispose();
        encoded.dispose();
    }

    private SoundWithDef loadInput(FilePath inputFile) {
        IEncodedResource inputAudioData = EncodedResource.fromFileSystem(resFileSystem, inputFile);

        ISoundDefinition soundDef = soundDefCache.getMetaData(inputFile);
        if (soundDef == null) {
            soundDef = new SoundDefinition(inputFile.getName());
        }

        return new SoundWithDef(inputAudioData, soundDef);
    }

    private ISoundEncoder createEncoder() {
        if (ffmpegAvailable != EAvailable.NO) {
            FfmpegSoundEncoder encoder = new FfmpegSoundEncoder(tempFileProvider);
            if (ffmpegAvailable == EAvailable.YES || encoder.isAvailable()) {
                ffmpegAvailable = EAvailable.YES;
                return encoder;
            }
        }
        ffmpegAvailable = EAvailable.NO;
        return new NoOpSoundEncoder();
    }

    private FilePath getOutputPath(FilePath inputPath, String outputFilename) {
        FilePath folder = inputPath.getParent();
        return folder.resolve(outputFilename);
    }

    private enum EAvailable {
        YES, NO, UNKNOWN
    }
}
