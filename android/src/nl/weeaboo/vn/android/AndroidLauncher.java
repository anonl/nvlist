package nl.weeaboo.vn.android;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;
import nl.weeaboo.vn.Launcher;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        // Android resources are in the root of the assets dir, not in a subfolder 'res/'
        initialize(new Launcher(""), config);
	}
}
