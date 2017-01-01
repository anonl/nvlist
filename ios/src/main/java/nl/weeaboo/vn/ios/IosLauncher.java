package nl.weeaboo.vn.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import nl.weeaboo.gdx.res.InternalGdxFileSystem;
import nl.weeaboo.vn.Launcher;

public final class IosLauncher extends IOSApplication.Delegate {

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        InternalGdxFileSystem resourceFileSystem = new InternalGdxFileSystem("");
        IosLocalFileSystem outputFileSystem = new IosLocalFileSystem();
        Launcher launcher = new Launcher(resourceFileSystem, outputFileSystem);

        return new IOSApplication(launcher, config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IosLauncher.class);
        pool.close();
    }

}