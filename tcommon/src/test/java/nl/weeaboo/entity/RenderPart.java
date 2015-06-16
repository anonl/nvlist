package nl.weeaboo.entity;

class RenderPart extends Part {

	private static final long serialVersionUID = 1L;
	
	public RenderPart() {
	}

	public void render(ModelPart model) {
		System.out.println("[render] " + model.toDetailedString());
	}
	
}
