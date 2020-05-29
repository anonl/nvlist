package nl.weeaboo.vn.buildtools.optimizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.optimizer.video.encoder.FfmpegVideoEncoder;

/**
 * Resource encoder using ffmpeg.
 */
public abstract class FfmpegEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(FfmpegVideoEncoder.class);

    private final ITempFileProvider tempFileProvider;

    protected FfmpegEncoder(ITempFileProvider tempFileProvider) {
        this.tempFileProvider = Checks.checkNotNull(tempFileProvider);
    }

    /**
     * @return {@code true} if a usable ffmpeg executable was found, allowing this encoder to be used.
     */
    public static boolean isAvailable() {
        try {
            doRunProcess(Arrays.asList("ffmpeg", "-h"));
            LOG.debug("ffmpeg is available");
            return true;
        } catch (IOException e) {
            LOG.info("ffmpeg not available: {}", e.toString());
            return false;
        }
    }

    protected IEncodedResource encode(IEncodedResource resource) throws IOException {
        File inputFile = tempFileProvider.newTempFile();
        File outputFile = tempFileProvider.newTempFile();

        IEncodedResource resultAudioData;
        try {
            // Copy sound to temp file (input)
            Files.write(resource.readBytes(), inputFile);

            runProcess(getCommandLineArgs(inputFile, outputFile));
            resultAudioData = EncodedResource.fromTempFile(outputFile);
        } finally {
            inputFile.delete();
        }

        return resultAudioData;
    }

    protected void runProcess(List<String> command) throws IOException {
        doRunProcess(command);
    }

    private static void doRunProcess(List<String> command) throws IOException {
        String commandString = Joiner.on(' ').join(command);

        Process process = new ProcessBuilder()
                .command(command)
                .redirectErrorStream(true)
                .start();

        StringBuilder output = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),
                    StandardCharsets.UTF_8));
            while (process.isAlive()) {
                String line = in.readLine();

                LOG.trace(line);

                output.append(line);
                output.append('\n');
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Process terminated with an error: " + exitCode
                        + "\ncommand: " + commandString
                        + "\noutput: " + output);
            }
        } catch (InterruptedException e) {
            throw new IOException("Process interrupted", e);
        }
    }

    private List<String> getCommandLineArgs(File inputFile, File outputFile) {
        List<String> command = Lists.newArrayList();
        command.add("ffmpeg");

        // Input file
        command.add("-i");
        command.add(inputFile.getAbsolutePath());

        // File format (container)
        command.add("-f");
        command.add(getFileFormat());

        // Codec
        command.addAll(getCodecArgs());

        // Output file
        command.add("-y"); // Overwrite output file (is usually an empty temp file)
        command.add(outputFile.getAbsolutePath());
        return command;
    }

    protected abstract List<String> getCodecArgs();

    protected abstract String getFileFormat();

}
