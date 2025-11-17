package net.kibotu.hexqrapp.ui.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.kibotu.hexqrapp.data.model.ParsedContentItem

@Composable
fun ParsedContentList(
    items: List<ParsedContentItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        return
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Parsed Content",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ParsedContentItemRow(item = item)
                }
            }
        }
    }
}

@Composable
private fun ParsedContentItemRow(item: ParsedContentItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.label}:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

