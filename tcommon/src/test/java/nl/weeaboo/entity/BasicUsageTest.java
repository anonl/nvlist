package nl.weeaboo.entity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class BasicUsageTest {

	/**
	 * Example usage of the API for a realistic scenario.
	 */
	@Test
	public void basicUsage() {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		
		Scene scene = world.createScene();
		
		// Add some entities we want to filter out
		Entity modelOnly = scene.createEntity();
		modelOnly.setPart(pr.typeModel, new ModelPart());
		
		Entity renderOnly = scene.createEntity();
		renderOnly.setPart(pr.typeRender, new RenderPart());

		// Add some entities with model+render parts
		RenderPart renderPart = new RenderPart();
		List<Entity> complete = new ArrayList<Entity>();
		for (int n = 0; n < 3; n++) {
			Entity e = scene.createEntity();
			e.setPart(pr.typeModel, new ModelPart(n+1, n+1, n+1));
			e.setPart(pr.typeRender, renderPart);
			complete.add(e);
		}
		
		EntityStream renderStream = scene.joinStream(new DefaultEntityStreamDef(pr.typeModel, pr.typeRender));
		
		Assert.assertEquals(3, renderStream.count());
		process(pr, renderStream);
		
		complete.get(0).removePart(pr.typeModel);
		
		Assert.assertEquals(2, renderStream.count());
		process(pr, renderStream);
	}
	
	private static void process(TestPartRegistry pr, EntityStream renderStream) {		
		for (Entity e : renderStream) {
			ModelPart m = e.getPart(pr.typeModel);
			RenderPart r = e.getPart(pr.typeRender);
			
			r.render(m); //Render the model
		}
	}
	
}
