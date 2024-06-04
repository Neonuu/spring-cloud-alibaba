package com.ushio.commonmodule.entity.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    //用户的用户名
    private static final String USERNAME = "admin";

    //用于签名加密的密钥，为一个字符串（需严格保密）
    private static final String KEY = "token_key";

    private static final int TOKEN_TIME_OUT = 1000 * 60 * 60 * 24;

    public static String getToken(String userId) {

        //获取jwt生成器
        JWTCreator.Builder jwtBuilder = JWT.create();

        //由于该生成器设置Header的参数为一个<String, Object>的Map,
        //所以我们提前准备好
        Map<String, Object> headers = new HashMap<>();

        headers.put("typ", "jwt");   //设置token的type为jwt
        headers.put("alg", "hs256");  //表明加密的算法为HS256

        //开始生成token
        //我们将之前准备好的header设置进去
        String token = jwtBuilder.withHeader(headers)

                //接下来为设置PayLoad,Claim中的键值对可自定义

                //设置用户id
                .withClaim("userId", userId)

                //token失效时间，这里为一天后失效
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_TIME_OUT))
                //设置该jwt的发行时间，一般为当前系统时间
                .withIssuedAt(new Date(System.currentTimeMillis()))

                //token的发行者（可自定义）
                .withIssuer(USERNAME)

                //进行签名，选择加密算法，以一个字符串密钥为参数
                .sign(Algorithm.HMAC256(KEY));

        //token生成完毕，可以发送给客户端了，前端可以使用
        //localStorage.setItem("your_token", token)进行存储，在
        //下次请求时携带发送给服务器端进行验证
        System.out.println(token);
        return token;
    }


    public static boolean verify(String token) {

        /*从请求头中获取token（具体要看你的token放在了请求的哪里，
           这里以放在请求头举例）
        */
        //String token = request.getHeader("token");

        /*判断token是否存在，若不存在，验证失败，
            并进行验证失败的逻辑操作（例如跳转到登录界面，
            或拒绝访问等等）*/
        if (token == null) return false;

        /*获取jwt的验证器对象，传入的算法参数以及密钥字符串（KEY）必须
        和加密时的相同*/
        JWTVerifier require = JWT.require(Algorithm.HMAC256(KEY)).build();

        DecodedJWT decode;
        try {

            /*开始进行验证，该函数会验证此token是否遭到修改，
                以及是否过期，验证成功会生成一个解码对象
                ，如果token遭到修改或已过期就会
                抛出异常，我们用try-catch抓一下*/
            decode = require.verify(token);

        } catch (Exception e) {

            //抛出异常，验证失败
            return false;
        }

        //若验证成功，就可获取其携带的信息进行其他操作

        //可以一次性获取所有的自定义参数，返回Map集合
        Map<String, Claim> claims = decode.getClaims();
        if (claims == null) return false;
        claims.forEach((k, v) -> System.out.println(k + " " + v.asString()));

        //也可以根据自定义参数的键值来获取
        if (decode.getClaim("userId") == null) return false;
        System.out.println(decode.getClaim("userId").toString());

        //获取发送者，没有设置则为空
        System.out.println(decode.getIssuer());

        //获取过期时间
        System.out.println(decode.getExpiresAt());

        //获取主题，没有设置则为空
        System.out.println(decode.getSubject());
        return true;
    }


}



