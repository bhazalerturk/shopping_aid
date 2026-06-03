package com.esselunga.navigator.data

/**
 * Item state after caregiver modifications
 */
enum class ItemChangeType {
    UNCHANGED,      // No change (grey)
    ADDED,          // Added (green)
    REMOVED,        // deleted (red)
    QUANTITY_CHANGED, // Quantity changed (yellow)
    COMMENT_ADDED   // commment added (blue) - future feature
}


data class ItemChange(
    val item: ShoppingItem,
    val changeType: ItemChangeType,
    val originalQuantity: Int? = null,  //QUANTITY_CHANGED
    val comment: String? = null         //COMMENT_ADDED
)

/**
 * Shows changes between original and modified shopping list, with details on what was added, removed or changed.
 * This is used to show the caregiver's modifications to the user before starting navigation.
 */
data class ListDiff(
    val originalItems: List<ShoppingItem>,
    val modifiedItems: List<ShoppingItem>,
    val changes: List<ItemChange>
) {
    companion object {
        fun calculate(original: List<ShoppingItem>, modified: List<ShoppingItem>): ListDiff {
            val changes = mutableListOf<ItemChange>()
            val originalIds = original.associateBy { it.id }
            val modifiedIds = modified.associateBy { it.id }

            originalIds.forEach { (id, originalItem) ->
                if (id !in modifiedIds) {
                    changes.add(ItemChange(originalItem, ItemChangeType.REMOVED))
                }
            }


            modifiedIds.forEach { (id, modifiedItem) ->
                val originalItem = originalIds[id]
                if (originalItem == null) {

                    changes.add(ItemChange(modifiedItem, ItemChangeType.ADDED))
                } else if (originalItem.quantity != modifiedItem.quantity) {

                    changes.add(
                        ItemChange(
                            modifiedItem,
                            ItemChangeType.QUANTITY_CHANGED,
                            originalQuantity = originalItem.quantity
                        )
                    )
                }
                // in the future and add a COMMENT_ADDED type
            }

            return ListDiff(original, modified, changes)
        }
    }
}
