package de.christian2003.smarthome.data.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import de.christian2003.smarthome.data.R


/**
 * Composable displays an info card informing the user about some stuff.
 *
 * @param message           Message to display to the user.
 * @param expandedMessage   Message displayed when the card is expanded. The card is not expandable
 *                          if null is passed.
 * @param iconResource      Resource ID of the icon to display.
 * @param backgroundColor   Background color for the card.
 * @param foregroundColor   Foreground color for the card.
 * @param isExpanded        Whether the expanded message is shown.
 * @param isExpandedChange  Callback invoked once the expanded message is shown / hidden.
 */
@Composable
fun SmartHomeInfoCard(
    message: String,
    expandedMessage: String? = null,
    iconResource: Int = R.drawable.ic_info,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    foregroundColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    isExpanded: Boolean = false,
    isExpandedChange: ((Boolean) -> Unit)? = null
) {
    val expanded = remember { mutableStateOf(isExpanded) }
    val paddingBottom: Dp = if (expandedMessage == null) { dimensionResource(R.dimen.space_vertical) } else { dimensionResource(R.dimen.space_vertical_between) }

    Column(
        modifier = Modifier
            .padding(
                start = dimensionResource(R.dimen.space_horizontal),
                end = dimensionResource(R.dimen.space_horizontal),
                bottom = dimensionResource(R.dimen.space_vertical)
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corners_default)))
            .border(
                width = dimensionResource(R.dimen.borders_default),
                color = foregroundColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.corners_default))
            )
            .background(backgroundColor)
            .clickable(enabled = !expandedMessage.isNullOrEmpty(), onClick = {
                expanded.value = !expanded.value
                if (isExpandedChange != null) {
                    isExpandedChange(expanded.value)
                }
            })
            .padding(
                start = dimensionResource(R.dimen.space_horizontal),
                top = dimensionResource(R.dimen.space_vertical),
                end = dimensionResource(R.dimen.space_horizontal),
                bottom = paddingBottom,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = dimensionResource(R.dimen.space_horizontal_between)),
                painter = painterResource(iconResource),
                tint = foregroundColor,
                contentDescription = ""
            )
            Text(
                modifier = Modifier.weight(1f),
                text = message,
                color = foregroundColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (!expandedMessage.isNullOrEmpty()) {
            Icon(
                painter = if (expanded.value) { painterResource(R.drawable.ic_collapse) } else { painterResource(R.drawable.ic_expand) },
                tint = foregroundColor,
                contentDescription = ""
            )
            AnimatedVisibility(expanded.value) {
                Text(
                    text = expandedMessage,
                    color = foregroundColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.space_vertical_between))
                )
            }
        }
    }
}
