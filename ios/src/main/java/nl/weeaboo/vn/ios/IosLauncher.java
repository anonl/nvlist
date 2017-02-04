package nl.weeaboo.vn.ios;

import java.lang.Thread.UncaughtExceptionHandler;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import nl.weeaboo.vn.gdx.res.InternalGdxFileSystem;
import nl.weeaboo.vn.impl.Launcher;

public final class IosLauncher extends IOSApplication.Delegate {

    @Override
    protected IOSApplication createApplication() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                /*
                 * Avoid instantiating LoggerFactory in a static context to work around a race condition
                 * caused by IOSApplication changing System.out/System.err
                 */
                Logger logger = LoggerFactory.getLogger(IosLauncher.class);
                logger.error("Uncaught exception from {}", t, e);
            }
        });

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        InternalGdxFileSystem resourceFileSystem = new InternalGdxFileSystem("");
        IosLocalFileSystem outputFileSystem = new IosLocalFileSystem();
        Launcher launcher = new Launcher(resourceFileSystem, outputFileSystem);

        return new IOSApplication(launcher, config);
    }

    /**
     * Main entry point for iOS.
     */
    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IosLauncher.class);
        pool.close();
    }

}