package nl.weeaboo.vn.impl.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Splitter;

import nl.weeaboo.filesystem.FilePath;

public final class ResourceQualifiers implements Iterable<IResourceQualifier> {

    private final List<IResourceQualifier> qualifiers = new ArrayList<>();

    private ResourceQualifiers() {
    }

    /**
     * Extracts the qualifiers from each part of the given file path.
     */
    public static ResourceQualifiers fromPath(FilePath path) {
        ResourceQualifiers result = new ResourceQualifiers();
        for (String pathSegment : getPathSegments(path)) {
            for (String part : Splitter.on('-').split(pathSegment)) {
                IResourceQualifier qualifier = tryParseQualifier(part);
                if (qualifier != null) {
                    result.qualifiers.add(qualifier);
                }
            }
        }
        return result;
    }

    private static Iterable<String> getPathSegments(FilePath path) {
        Deque<String> stack = new ArrayDeque<>();
        while (path != null) {
            String name = path.getName();
            if (name.endsWith("/")) {
                // Strip trailing '/'
                stack.push(name.substring(0, name.length() - 1));
            } else {
                stack.push(name);
            }
            path = path.getParent();
        }
        return stack;
    }

    private static @Nullable IResourceQualifier tryParseQualifier(String string) {
        return SizeQualifier.tryParse(string);
    }

    /**
     * Appends the given qualifier to the first (top-level) folder in the given path.
     */
    public static FilePath applyToRootFolder(FilePath path, IResourceQualifier qualifier) {
        String pathString = path.toString();
        // Replace "abc/" with "abc-qualifier/"
        pathString = pathString.replaceFirst("([^/]+)[/]",
                "$1-" + qualifier.toPathString() + "/");
        return FilePath.of(pathString);
    }

    @Override
    public Iterator<IResourceQualifier> iterator() {
        return Collections.unmodifiableList(qualifiers).iterator();
    }

    /**
     * Returns the qualifier with the given type (if any).
     *
     * @return A matching qualifier, or {@code null} if not found.
     */
    public @Nullable <T extends IResourceQualifier> T findQualifier(Class<T> type) {
        for (IResourceQualifier qualifier : qualifiers) {
            if (type.isInstance(qualifier)) {
                return type.cast(qualifier);
            }
        }
        return null;
    }
}
