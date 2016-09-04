package nl.weeaboo.vn.sound.impl.desc;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

public class SoundDefinitionIOTest {

    /** Simple definition with no subrects */
    @Test
    public void minimal() throws IOException {
        SoundDefinition def = Iterables.getOnlyElement(load("minimal.json"));
        Assert.assertEquals("minimal", def.getFile().toString());
        Assert.assertEquals(null, def.getDisplayName());
    }

    @Test
    public void allattrs() throws IOException {
        SoundDefinition def = Iterables.getOnlyElement(load("allattrs.json"));
        Assert.assertEquals("allattrs", def.getFile().toString());
        Assert.assertEquals("MyDisplayName", def.getDisplayName());
    }

    private Collection<SoundDefinition> load(String path) throws IOException {
        String content = Resources.toString(getClass().getResource("/sounddesc/" + path), Charsets.UTF_8);
        return SoundDefinitionIO.deserialize(content);
    }

}
