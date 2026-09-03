package com.lab.island

import com.lab.island.island.ExpandedAccessory
import com.lab.island.island.ExpandedTemplate
import com.lab.island.island.LargeIslandTemplate
import com.lab.island.island.SmallIslandTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IslandTemplateCatalogTest {
    @Test
    fun expandedCatalog_containsEveryOfficialCombination() {
        assertEquals(22, ExpandedTemplate.entries.size)
        assertEquals(
            setOf(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14-1", "14-2", "15", "16", "17", "18", "19", "20", "21"
            ),
            ExpandedTemplate.entries.mapTo(mutableSetOf()) { it.number }
        )
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.PROGRESS_ONE })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.PROGRESS_TWO })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.MULTI_PROGRESS })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.ACTIONS })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.HINT_TWO })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.HINT_THREE })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.TEXT_BUTTONS })
        assertTrue(ExpandedTemplate.entries.any { it.accessory == ExpandedAccessory.HIGHLIGHT_ACTION })
    }

    @Test
    fun summaryCatalog_containsAllOfficialAreas() {
        assertEquals(9, LargeIslandTemplate.entries.size)
        assertEquals((1..9).toList(), LargeIslandTemplate.entries.map { it.number })
        assertEquals(3, SmallIslandTemplate.entries.size)
    }
}
