package nl.weeaboo.vn.buildtools.optimizer;

/**
 * Global quality profile for various optimizers.
 */
public enum OptimizerPreset {

    /** Only perform lossless optimizations */
    LOSSLESS,

    /** Allow lossy optimizations, balance between quality and size */
    MEDIUM;

}
