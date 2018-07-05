package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionBuilder;

public final class FfmpegSoundEncoder implements ISoundEncoder {

    private final ITempFileProvider tempFileProvider;

    public FfmpegSoundEncoder(ITempFileProvider tempFileProvider) {
        this.tempFileProvider = Checks.checkNotNull(tempFileProvider);
    }

    @Override
    public EncodedSound encode(SoundWithDef sound) throws IOException {
        File inputFile = tempFileProvider.newTempFile();
        File outputFile = tempFileProvider.newTempFile();

        IEncodedResource resultAudioData;
        try {
            // Copy sound to temp file (input)
            Files.write(sound.getAudioData().readBytes(), inputFile);

            runEncoderProcess(getCommandLineArgs(inputFile, outputFile));
            resultAudioData = EncodedResource.fromTempFile(outputFile);
        } finally {
            inputFile.delete();
        }

        SoundDefinitionBuilder soundDef = sound.getDef().builder();
        soundDef.setFilename(Filenames.replaceExt(soundDef.getFilename(), "ogg"));
        return new EncodedSound(resultAudioData, soundDef.build());
    }

    private void runEncoderProcess(List<String> command) throws IOException {
        String commandString = Joiner.on(' ').join(command);

        Process process = new ProcessBuilder()
                .command(command)
                .redirectOutput(Redirect.INHERIT)
                .redirectErrorStream(true)
                .start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Process terminated with an error: " + exitCode
                        + "\ncommand: " + commandString);
            }
        } catch (InterruptedException e) {
            throw new IOException("Process interrupted", e);
        }
    }

    private List<String> getCommandLineArgs(File inputFile, File outputFile) {
        // ffmpeg -i input.ogg -codec:a libvorbis -qscale:a 3 output.ogg

        List<String> command = Lists.newArrayList();
        command.add("ffmpeg");

        // Input file
        command.add("-i");
        command.add(inputFile.getAbsolutePath());

        // File format (container)
        command.add("-f");
        command.add("ogg");

        // Audio codec
        command.add("-codec:a");
        command.add("libvorbis");
        command.add("-qscale:a");
        command.add("3");

        // Output file
        command.add("-y"); // Overwrite output file (is usually an empty temp file)
        command.add(outputFile.getAbsolutePath());
        return command;
    }

}
