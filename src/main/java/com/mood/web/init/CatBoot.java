package com.mood.web.init;

import com.mood.framework.minicat.AppRun;
import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.web.servlet.TestServlet;

import java.util.Scanner;

public class CatBoot {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("请选择cat的模式 1.bio 2.nio 3.netty");
        Integer model =scanner.nextInt();
        System.out.println("请选择端口");
        Integer prot=scanner.nextInt();
        MoodCatConfig.HTTP_PORT=prot;
        MoodCatConfig.MODEL=model;
        AppRun.init(TestServlet.class);

    }
}
