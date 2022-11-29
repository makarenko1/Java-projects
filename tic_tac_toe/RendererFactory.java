/**
 * The Factory class for the Renderer module.
 */
public class RendererFactory {

    private static final String CONSOLE_RENDERER = "console";
    private static final String VOID_RENDERER = "none";

    /**
     * Creates a game board renderer according to the given type.
     * @param rendererType the type of the renderer.
     * @return an object of the class of the given type that implements the interface Renderer.
     */
    Renderer buildRenderer(String rendererType) {
        switch (rendererType) {
            case CONSOLE_RENDERER:
                return new ConsoleRenderer();
            case VOID_RENDERER:
                return new VoidRenderer();
            default:
                return null;
        }
    }
}
