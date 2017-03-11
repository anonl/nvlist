package nl.weeaboo.vn.impl.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.prefsstore.Preference;

public class NovelPrefsStoreTest {

    @Test
    public void testGetDeclaredPrefs() {
        List<Preference<?>> declared = NovelPrefsStore.getDeclaredPrefs(TestPrefsHolder.class);
        Assert.assertEquals(TestPrefsHolder.getAllPrefs(), ImmutableSet.copyOf(declared));
    }

}
