package de.christian2003.smarthome.data.view.room

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.christian2003.smarthome.data.R
import de.christian2003.smarthome.data.model.devices.ShGenericDevice
import de.christian2003.smarthome.data.model.devices.ShLight
import de.christian2003.smarthome.data.model.devices.ShOpening
import de.christian2003.smarthome.data.model.devices.ShOutlet
import de.christian2003.smarthome.data.model.devices.ShShutter
import de.christian2003.smarthome.data.model.devices.ShUnknownDevice
import de.christian2003.smarthome.data.model.room.ShInfoText
import de.christian2003.smarthome.data.model.userinformation.InformationType
import de.christian2003.smarthome.data.model.userinformation.UserInformation
import de.christian2003.smarthome.data.ui.utils.SmartHomeInfoCard


/**
 * Composable displays a room and it's content.
 *
 * @param viewModel     View model for the view.
 * @param onNavigateUp  Callback to invoke in order to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomView(
    viewModel: RoomViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewModel.room.name,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ItemsList(
            items = viewModel.items,
            serverUrl = viewModel.serverUrl,
            imageLoader = viewModel.imageLoader,
            showWarnings = viewModel.showWarnings,
            showErrors = viewModel.showErrors,
            onError = { message ->
                viewModel.onImageError(message)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}


@Composable
fun ItemsList(
    items: List<Any>,
    serverUrl: String,
    imageLoader: ImageLoader?,
    showWarnings: Boolean,
    showErrors: Boolean,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items) { item ->
            when (item) {
                is ShGenericDevice -> ListRowGeneric(
                    device = item,
                    serverUrl = serverUrl,
                    imageLoader = imageLoader,
                    onError = onError
                )
                //is ShOpening -> ListRowOpening(item)
                //is ShOutlet -> ListRowOutlet(item)
                //is ShShutter -> ListRowShutter(item)
                is ShInfoText -> ListRowText(item)
                is UserInformation -> ListRowWarning(item, showWarnings, showErrors)
                //is ShUnknownDevice -> ListRowUnknown(item)
            }
        }
    }
}


/**
 * Composable displays a light.
 *
 * @param device    Light to display.
 */
@Composable
fun ListRowLight(
    device: ShLight
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (device.specifier != null) { device.specifier!! } else { device.name },
                color = MaterialTheme.colorScheme.onSurface
            )
            if (device.milliAmp != null) {
                Text(
                    text = device.milliAmp!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.hours != null) {
                Text(
                    text = device.hours!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.wh != null) {
                Text(
                    text = device.wh!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (device.onButtonText != null) {
            TextButton(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between)),
                onClick = {
                    //TODO: Handle
                }
            ) {
                Text(device.onButtonText!!)
            }
        }
        if (device.offButtonText != null) {
            TextButton(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between)),
                onClick = {
                    //TODO: Handle
                }
            ) {
                Text(device.offButtonText!!)
            }
        }
    }
}


/**
 * Composable displays an opening.
 *
 * @param device    Opening to display.
 */
@Composable
fun ListRowOpening(
    device: ShOpening
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            )
    ) {
        Text(
            text = if (device.specifier != null) { device.specifier!! } else { device.name },
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


/**
 * Composable displays an outlet.
 *
 * @param device    Outlet to display.
 */
@Composable
fun ListRowOutlet(
    device: ShOutlet
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (device.specifier != null) { device.specifier!! } else { device.name },
                color = MaterialTheme.colorScheme.onSurface
            )
            if (device.amperage != null) {
                Text(
                    text = device.amperage!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.time != null) {
                Text(
                    text = device.time!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.powerConsumption != null) {
                Text(
                    text = device.powerConsumption!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (device.onButtonText != null) {
            TextButton(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between)),
                onClick = {
                    //TODO: Handle
                }
            ) {
                Text(device.onButtonText!!)
            }
        }
        if (device.offButtonText != null) {
            TextButton(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between)),
                onClick = {
                    //TODO: Handle
                }
            ) {
                Text(device.offButtonText!!)
            }
        }
    }
}


/**
 * Composable displays a shutter.
 *
 * @param device    Shutter to display.
 */
@Composable
fun ListRowShutter(
    device: ShShutter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (device.specifier != null) { device.specifier!! } else { device.name },
                color = MaterialTheme.colorScheme.onSurface
            )
            if (device.percentage != null) {
                Text(
                    text = device.percentage!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.time != null) {
                Text(
                    text = device.time!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (device.setButtonText != null) {
            TextButton(
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between)),
                onClick = {
                    //TODO: Handle
                }
            ) {
                Text(device.setButtonText!!)
            }
        }
    }
}


/**
 * Composable displays an unknown device.
 *
 * @param device    Unknown device to display.
 */
@Composable
fun ListRowUnknown(
    device: ShUnknownDevice
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (device.specifier != null) { device.specifier!! } else { device.name },
                color = MaterialTheme.colorScheme.onSurface
            )
            if (device.milliAmp != null) {
                Text(
                    text = device.milliAmp!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.hours != null) {
                Text(
                    text = device.hours!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (device.wh != null) {
                Text(
                    text = device.wh!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun ListRowGeneric(
    device: ShGenericDevice,
    serverUrl: String,
    imageLoader: ImageLoader?,
    onError: (String) -> Unit
) {
    val context: Context = LocalContext.current

    val url: String? = if(device.imageUri != null) {
        val imageUri: String? = if (device.imageUri != null && device.imageUri!!.contains("/sh/")) {
            device.imageUri!!.replace("/sh/", "")
        } else {
            device.imageUri
        }
        (serverUrl + imageUri).toUri().toString()
    } else {
        null
    }

    val imageRequest: ImageRequest? = if (url != null) {
        ImageRequest.Builder(context)
            .data(url)
            .listener(
                onError = { _, result ->
                    val message: String = if (result.throwable.message != null && result.throwable.message!!.isNotEmpty()) {
                        "${result.throwable.message!!}\n\nURL: $url"
                    } else {
                        "An unknown error occurred while loading an image\n\nURL: $url"
                    }
                    onError(message)
                }
            )
            .build()
    } else {
        null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        if (imageLoader != null && imageRequest != null) {
            AsyncImage(
                model = imageRequest,
                contentDescription = "",
                imageLoader = imageLoader,
                placeholder = painterResource(R.drawable.ic_image),
                error = painterResource(R.drawable.ic_error),
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.space_horizontal_between))
                    .size(dimensionResource(R.dimen.image_large)),
            )
        }
        Column {
            Text(
                text = device.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            if (device.specifier != null) {
                Text(
                    text = device.specifier!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


/**
 * Composable displays an info text.
 *
 * @param text  Info text to display.
 */
@Composable
fun ListRowText(
    text: ShInfoText
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            )
    ) {
        if (text.text != null) {
            Text(
                text = if (text.specifier != null) { text.specifier!! } else { text.label },
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = text.text!!,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/**
 * Composable displays a warning or error message.
 *
 * @param information   Information to display as warning or error.
 * @param showWarnings  Whether to display warnings.
 * @param showErrors    Whether to display errors.
 */
@Composable
fun ListRowWarning(
    information: UserInformation,
    showWarnings: Boolean,
    showErrors: Boolean
) {
    if (information.informationType == InformationType.WARNING && showWarnings) {
        SmartHomeInfoCard(
            message = information.informationTitle.getText(LocalContext.current),
            expandedMessage = information.description,
            iconResource = R.drawable.ic_warning,
            foregroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            isExpanded = information.isDescriptionVisible,
            isExpandedChange = {
                information.isDescriptionVisible = it
            }
        )
    }
    else if (information.informationType == InformationType.ERROR && showErrors) {
        SmartHomeInfoCard(
            message = information.informationTitle.getText(LocalContext.current),
            expandedMessage = information.description,
            iconResource = R.drawable.ic_error,
            foregroundColor = MaterialTheme.colorScheme.onErrorContainer,
            backgroundColor = MaterialTheme.colorScheme.errorContainer,
            isExpanded = information.isDescriptionVisible,
            isExpandedChange = {
                information.isDescriptionVisible = it
            }
        )
    }
}
