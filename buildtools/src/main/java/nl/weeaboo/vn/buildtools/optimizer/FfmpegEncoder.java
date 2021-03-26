package nl.weeaboo.vn.buildtools.optimizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;

/**
 * Resource encoder using ffmpeg.
 */
public abstract class FfmpegEncoder {

    private final Logger logger;
    private final ITempFileProvider tempFileProvider;

    private String program = "ffmpeg";

    protected FfmpegEncoder(Logger logger, ITempFileProvider tempFileProvider) {
        this.logger = Checks.checkNotNull(logger);
        this.tempFileProvider = Checks.checkNotNull(tempFileProvider);
    }

    /**
     * @return {@code true} if a usable ffmpeg executable was found, allowing this encoder to be used.
     */
    public boolean isAvailable() {
        try {
            doRunProcess(program, Arrays.asList("-h"));
            logger.debug("ffmpeg is available");
            return true;
        } catch (IOException e) {
            logger.info("ffmpeg not available: {}", e.toString());
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

            runProcess(program, getCommandLineArgs(inputFile, outputFile));
            resultAudioData = EncodedResource.fromTempFile(outputFile);
        } finally {
            inputFile.delete();
        }

        return resultAudioData;
    }

    protected void runProcess(String program, List<String> args) throws IOException {
        doRunProcess(program, args);
    }

    private void doRunProcess(String program, List<String> args) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(program);
        command.addAll(args);
        String commandString = Joiner.on(' ').join(command);

        logger.trace("Starting process: {}", command);

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

                logger.trace(line);

                output.append(line);
                output.append('\n');
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException(StringUtil.formatRoot(
                        "Process terminated with an error: %s\ncommand: %s\noutput: %s",
                        exitCode, commandString, output));
            }
        } catch (InterruptedException e) {
            throw new IOException("Process interrupted", e);
        }
    }

    private List<String> getCommandLineArgs(File inputFile, File outputFile) {
        List<String> args = Lists.newArrayList();

        // Input file
        args.add("-i");
        args.add(inputFile.getAbsolutePath());

        // File format (container)
        args.add("-f");
        args.add(getFileFormat());

        // Codec
        args.addAll(getCodecArgs());

        // Output file
        args.add("-y"); // Overwrite output file (is usually an empty temp file)
        args.add(outputFile.getAbsolutePath());
        return args;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    protected abstract List<String> getCodecArgs();

    protected abstract String getFileFormat();

}
