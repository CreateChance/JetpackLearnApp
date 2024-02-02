package com.example.compose00

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose00.data.SimpleData
import com.example.compose00.model.Message
import com.example.compose00.ui.theme.JetpackLearnAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackLearnAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Conversation(messages = SimpleData.conversationSample)
                }
            }
        }
    }
}

@Composable
fun MessageReceivedCard(msg: Message, isExpanded: Boolean, onClicked: (() -> Unit)? = null) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(id = R.drawable.avatar00),
            contentDescription = " Contract profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        // add a horizontal space between the image and the column.
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.clickable { onClicked?.invoke() }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
            }
        }
    }
}

@Composable
fun MessageSentCard(msg: Message, isExpanded: Boolean, onClicked: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(modifier = Modifier.clickable { onClicked?.invoke() }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(alignment = Alignment.End)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
            }
        }

        // add a horizontal space between the image and the column.
        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(id = R.drawable.avatar01),
            contentDescription = " Contract profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

@Composable
fun Conversation(messages: List<Message>) {
    // 因为我们这里使用的是 Message 对象，默认的 Bundle 中不能存储这个类型，必须将它实现为 Parcelable 类型才行
    // 同时，需要自定义 saver
    val selectedMessages = rememberSaveable(saver = listSaver(
        save = { stateList ->
            if (stateList.isNotEmpty()) {
                val first = stateList.first()
                if (!canBeSaved(first)) {
                    throw IllegalStateException("${first::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
                }
            }
            stateList.toList()
        },
        restore = { it.toMutableStateList() }
    )) { mutableStateListOf<Message>() }

    LazyColumn {
        items(messages) { message ->
            val isExpanded = selectedMessages.contains(message)
            if (message.isMe()) {
                MessageSentCard(msg = message, isExpanded) {
                    if (selectedMessages.contains(message)) {
                        selectedMessages.remove(message)
                    } else {
                        selectedMessages.add(message)
                    }
                }
            } else {
                MessageReceivedCard(msg = message, isExpanded) {
                    if (selectedMessages.contains(message)) {
                        selectedMessages.remove(message)
                    } else {
                        selectedMessages.add(message)
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ConversationLightPreview() {
    JetpackLearnAppTheme {
        Surface {
            Conversation(messages = SimpleData.conversationSample)
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MessageReceivedCardLightPreview() {
    JetpackLearnAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MessageReceivedCard(
                Message("Lexi", "Take a look at jetpack Compose, it's great!"),
                true,
            )
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MessageReceivedCardDarkPreview() {
    JetpackLearnAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MessageReceivedCard(
                Message("Lexi", "Take a look at jetpack Compose, it's great!"),
                true
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MessageSentCardLightPreview() {
    JetpackLearnAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MessageSentCard(
                Message("CreateChance", "Take a look at jetpack Compose, it's great!"),
                true
            )
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MessageSentCardDarkPreview() {
    JetpackLearnAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MessageSentCard(
                Message("CreateChance", "Take a look at jetpack Compose, it's great!"),
                true
            )
        }
    }
}
