package nl.weeaboo.entity;

final class TestPartRegistry extends PartRegistry {

	private static final long serialVersionUID = 1L;

	public final PartType<ModelPart> typeA;
	public final PartType<ModelPart> typeB;
	public final PartType<ModelPart> typeC;
	public final PartType<ModelPart> typeModel;
	public final PartType<RenderPart> typeRender;

	public TestPartRegistry() {
        typeA = register("a", ModelPart.class);
        typeB = register("b", ModelPart.class);
        typeC = register("c", ModelPart.class);
        typeModel = register("model", ModelPart.class);
        typeRender = register("render", RenderPart.class);
	}

}
