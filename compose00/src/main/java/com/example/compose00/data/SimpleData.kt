package com.example.compose00.data

import com.example.compose00.model.Message

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/2/2
 */
class SimpleData {
    companion object {
        val conversationSample = listOf(
            Message("Lexi", "Test...Test...Test..."),
            Message("Lexi", "Compose 编程思想"),
            Message("CreateChance", "很好！马上了解下！"),
            Message("Lexi", "可组合函数: 了解 Compose 的构建块：可组合函数。"),
            Message("CreateChance", "什么是可组合函数？函数可以组合吗？"),
            Message(
                "Lexi",
                "编写您的第一个 Compose 应用。开展动手实践，了解声明式界面的基础知识，试用状态、布局和主题设置。您将看到什么是可组合项和修饰符，如何使用基本界面元素（如行和列），以及如何为应用指定状态。"
            ),
            Message(
                "Lexi",
                "Compose 界面工具包。了解 Compose 工具包中的一系列可组合项：Material Design 组件、布局、修饰符等。"
            ),
            Message(
                "Lexi",
                "实现真实的设计。在此 Codelab 中，您将了解如何通过使用由 Compose 以开箱即用的方式提供的可组合项和修饰符来实现真实的设计。"
            ),
            Message("CreateChance", "很棒的课程！学到了很多东西。"),
            Message(
                "Lexi",
                "状态使用入门。了解应用的状态如何决定界面中显示的内容、Compose 如何在状态发生变化时更新界面、如何优化可组合函数的结构，以及如何在 Compose 应用中使用 ViewModel。"
            ),
            Message("CreateChance", "状态还有一点复杂，需要点时间理解下。"),
            Message(
                "Lexi",
                "利用工具加快开发速度。了解如何使用 Android Studio 中的 Compose 专用工具加速应用开发。"
            ),
            Message(
                "Lexi",
                "从 View 系统迁移。已有一个使用 View 构建的现有应用？了解如何采用增量方式进行迁移。"
            ),
            Message("CreateChance", "Wow！入门了，解决了很多疑惑！"),
            Message(
                "Lexi",
                "实操迁移：分步介绍如何将基于 View 的应用实际迁移到 Jetpack Compose，以了解如何逐步采用 Compose，并探索其对架构和测试的影响。"
            ),
            Message("CreateChance", "感谢精彩的课程！"),
        )
    }
}