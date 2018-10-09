package nl.weeaboo.vn.impl.core;

/**
 * Represents a resource classifier that's added onto a resource folder name. For example, a size qualifier
 * can be added to the image folder ("img/" -&gt; "img-1280x720/"). This allows you to have multiple versions
 * of the same resource side-by-side, letting the engine decide the most appropriate variant.
 */
public interface IResourceQualifier {

    /**
     * Encodes the qualifier as a string for use in a folder name.
     */
    String toPathString();

}
