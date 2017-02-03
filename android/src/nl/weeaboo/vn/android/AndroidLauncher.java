package nl.weeaboo.vn.android;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.Launcher;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitConfig.init();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        // Android resources are in the root of the assets dir, not in a subfolder 'res/'
        AndroidAssetFileSystem resourceFileSystem = new AndroidAssetFileSystem();
        IWritableFileSystem outputFileSystem = new AndroidLocalFileSystem();

        initialize(new Launcher(resourceFileSystem, outputFileSystem), config);
    }
}
