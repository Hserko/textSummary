package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class CozeApiServiceTest {

    @Resource
    private CozeApiService cozeApiService;

    @Test
    void callCozeWorkflowByHttp() {
        String input = "# 猫先生的奇妙代码之旅\n" +
                "在程序员小镇，住着猫先生。他总对着电脑屏幕里的代码发愁，那些复杂的逻辑像缠成一团的毛线球，让他无从下手。\n" +
                "\n" +
                "这天，猫先生又被一个`NullPointerException`（空指针异常 ）搞得抓耳挠腮，趴在键盘上唉声叹气。突然，屏幕里射出一道光，把他吸了进去。\n" +
                "\n" +
                "猫先生掉进了一个全是代码的世界，脚下是一行行滚动的代码，头顶飘着各种数据云朵。一只戴着眼镜的代码精灵跳出来：“欢迎来到代码宇宙，这里的每个错误都会化作挑战，解决了才能回去哟。”\n" +
                "\n" +
                "猫先生遇到的第一个关卡，是一段永远循环的代码。代码精灵说：“这是`InfiniteLoop`（无限循环 ）怪物，快找出让它停下的办法！” 猫先生仔细看，发现是少了循环终止条件，他加上`break`语句，循环乖乖停下，还掉落了“逻辑清晰宝石”。\n" +
                "\n" +
                "接着，他们走到一道满是破碎路径的桥边，代码精灵解释：“这是`404Error`（资源未找到错误 ）桥，只有修复路径才能通过。” 猫先生回忆起在Spring Boot里配置接口路由的知识，重新梳理路径，桥瞬间修复，又获得“精准路由徽章” 。\n" +
                "\n" +
                "最后一关，是和`ConcurrentModificationException`（并发修改异常 ）大魔王对决。大魔王操控着多线程，数据被搅得一团糟。猫先生想起用`ConcurrentHashMap`（并发哈希表 ），让多线程安全访问数据，大魔王被打败，他拿到“线程安全皇冠” 。\n" +
                "\n" +
                "集齐宝物的猫先生回到现实，再看代码，那些错误变得清晰。他用在代码宇宙学到的本事，轻松解决问题，从那以后，猫先生成了程序员小镇最厉害的“代码骑士”，把奇妙经历讲给每只爱编程的小猫听 。 ";
        System.out.println(cozeApiService.callCozeWorkflowByHttp(input,""));
    }

    @Test
    void callCozeWorkflowBySdk() {
        String input = "# 猫先生的奇妙代码之旅\n" +
                "在程序员小镇，住着猫先生。他总对着电脑屏幕里的代码发愁，那些复杂的逻辑像缠成一团的毛线球，让他无从下手。\n" +
                "\n" +
                "这天，猫先生又被一个`NullPointerException`（空指针异常 ）搞得抓耳挠腮，趴在键盘上唉声叹气。突然，屏幕里射出一道光，把他吸了进去。\n" +
                "\n" +
                "猫先生掉进了一个全是代码的世界，脚下是一行行滚动的代码，头顶飘着各种数据云朵。一只戴着眼镜的代码精灵跳出来：“欢迎来到代码宇宙，这里的每个错误都会化作挑战，解决了才能回去哟。”\n" +
                "\n" +
                "猫先生遇到的第一个关卡，是一段永远循环的代码。代码精灵说：“这是`InfiniteLoop`（无限循环 ）怪物，快找出让它停下的办法！” 猫先生仔细看，发现是少了循环终止条件，他加上`break`语句，循环乖乖停下，还掉落了“逻辑清晰宝石”。\n" +
                "\n" +
                "接着，他们走到一道满是破碎路径的桥边，代码精灵解释：“这是`404Error`（资源未找到错误 ）桥，只有修复路径才能通过。” 猫先生回忆起在Spring Boot里配置接口路由的知识，重新梳理路径，桥瞬间修复，又获得“精准路由徽章” 。\n" +
                "\n" +
                "最后一关，是和`ConcurrentModificationException`（并发修改异常 ）大魔王对决。大魔王操控着多线程，数据被搅得一团糟。猫先生想起用`ConcurrentHashMap`（并发哈希表 ），让多线程安全访问数据，大魔王被打败，他拿到“线程安全皇冠” 。\n" +
                "\n" +
                "集齐宝物的猫先生回到现实，再看代码，那些错误变得清晰。他用在代码宇宙学到的本事，轻松解决问题，从那以后，猫先生成了程序员小镇最厉害的“代码骑士”，把奇妙经历讲给每只爱编程的小猫听 。 ";
        System.out.println(cozeApiService.callCozeWorkflowBySdk(input,""));
    }
}