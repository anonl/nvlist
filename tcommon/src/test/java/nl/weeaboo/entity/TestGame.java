package nl.weeaboo.entity;

/*
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.weeaboo.game.GameLog;
import nl.weeaboo.game.GameLogFormatter;
import nl.weeaboo.game.desktop.AWTGame;
import nl.weeaboo.game.desktop.AWTGameBuilder;
import nl.weeaboo.game.desktop.AWTLauncher;
import nl.weeaboo.game.input.IUserInput;

import com.jogamp.newt.event.KeyEvent;

public class TestGame extends AWTGame {

	private static final File TEMP_FILE = new File("world.bin");
	
	private World world;
	private Scene mainScene;
	
	public TestGame(AWTGameBuilder b) {
		super(b);
	}
	
	public static void main(String[] args) {
		AWTLauncher l = new AWTLauncher() {
			@Override
			protected AWTGameBuilder newGameBuilder() {
				return new AWTGameBuilder() {
					@Override
					public AWTGame build() throws IllegalStateException {
						return new TestGame(this);
					}					
				};
			}
		};
		l.setGameId("test");
		AWTLauncher.main(l, args);
	}

	@Override
	protected boolean update(IUserInput input, float dt) {
		if (world == null) {
			//Ugly hacked-in initialization code goes here
			world = new World();
			mainScene = world.createScene();
			
			Logger entityLog = EntityLog.getInstance();
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.ALL);
			ch.setFormatter(new GameLogFormatter(false, true));
			entityLog.addHandler(ch);
			
			setOSDVisible(true);
		}
		
		boolean changed = false;
		
		try {
			if (input.consumeKey(KeyEvent.VK_1)) {
				TestUtil.serializeWorld(TEMP_FILE, true, world);
				getNotifier().addMessage(this, "Written: " + mainScene.getEntitiesCount());
			} else if (input.consumeKey(KeyEvent.VK_2)) {
				world = TestUtil.deserializeWorld(TEMP_FILE);
				mainScene = world.getScene(mainScene.getId());
				
				getNotifier().addMessage(this, "Read: " + mainScene.getEntitiesCount());
			} else if (input.consumeKey(KeyEvent.VK_3)) {
				createEntity(3);
			}
		} catch (IOException ioe) {
			GameLog.e("Exception", ioe);
		} catch (ClassNotFoundException cnfe) {
			GameLog.e("Exception", cnfe);
		}
		
		changed |= super.update(input, dt);
		
		return changed;
	}
	
	@Override
	public String generateOSDText() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(super.generateOSDText());
		sb.append("\n");
		
		sb.append(String.format("Entities: %d\n", mainScene.getEntities().size()));
		
		return sb.toString();
	}
	
	private Entity createEntity(int type) {
		Entity entity = mainScene.createEntity();
		return entity;
	}
	
}
*/