package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.i18n.LocalAppLanguage
import org.example.project.i18n.stringsFor
import org.example.project.viewmodel.ChecklistItem
import org.example.project.viewmodel.CrisisViewModel

@Composable
fun ChecklistScreen() {
    val s = stringsFor(LocalAppLanguage.current)

    var items by remember {
        mutableStateOf(
            listOf(
                ChecklistItem("1", "🗣️ ${s.categoryComm}", s.commAnnounce),
                ChecklistItem("2", "🗣️ ${s.categoryComm}", s.commCheckStatus),
                ChecklistItem("3", "🔍 ${s.categoryAssess}", s.assessDangers),
                ChecklistItem("4", "🔍 ${s.categoryAssess}", s.assessInjured),
                ChecklistItem("5", "🏃 ${s.categoryAct}", s.actLocation),
                ChecklistItem("6", "🏃 ${s.categoryAct}", s.actHeadMeeting),
                ChecklistItem("7", "🏃 ${s.categoryAct}", s.actCall112)
            )
        )
    }

    val completedCount = items.count { it.isChecked }
    val totalCount = items.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = s.emergencyChecklist,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = s.completedOf.replaceFirst("%d", completedCount.toString()).replaceFirst("%d", totalCount.toString()),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val groupedItems = items.groupBy { it.category }
            
            groupedItems.forEach { (category, categoryItems) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                items(categoryItems, key = { it.id }) { checklistItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                items = items.map {
                                    if (it.id == checklistItem.id) it.copy(isChecked = !it.isChecked) else it
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checklistItem.isChecked,
                            onCheckedChange = { checked ->
                                items = items.map {
                                    if (it.id == checklistItem.id) it.copy(isChecked = checked) else it
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = checklistItem.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (checklistItem.isChecked) Color.Gray else MaterialTheme.colorScheme.onBackground,
                            textDecoration = if (checklistItem.isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    }
                }
            }
        }
    }
}
