package app.com.seehope.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import leap.web.exception.ResponseException;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * @Author: 龍右
 * @Date: 2020/1/4 15:21
 * @Description:
 */
@Slf4j
@Builder
@Data
public class JwtTokenUtils {
    /**
     * 传输信息，必须是json格式
     */
    private String msg;
    /**
     * 所验证的jwt
     */
    private String token;

    private final String secret="sfsafsa52123d15647a61das+_02134adpzocq";

    public static List<String> tokenList= new ArrayList<>();

    /**
     * 加密
     * @return
     */
    public String creatJwtToken () {
        msg = new AESUtils(msg).encrypt();//先对信息进行aes加密(防止被破解）
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("zjf")
                    .withExpiresAt(DateTime.now().plusDays(1).toDate())
                    .withClaim("user", msg)
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception e) {
            throw e;
        }
        return token;
    }
    /**
     * 解密jwt并验证是否正确
     */
    public String freeJwt (String token) {
        DecodedJWT decodedJWT = null;
        try {
            //使用hmac256加密算法
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("zjf")
                    .build();
            decodedJWT = verifier.verify(token);
            log.info("签名人：" + decodedJWT.getIssuer() + " 加密方式：" + decodedJWT.getAlgorithm() + " 携带信息：" + decodedJWT.getClaim("user").asString());
        } catch (Exception e) {
            log.info("jwt解密出现错误，jwt或私钥或签证人不正确");
            throw new ResponseException(ErrorConstant.INTERNALSERVERERROR,"jwt解密出现错误,jwt或私钥或签证人不正确");
        }
        //获得token的头部，载荷和签名，只对比头部和载荷
        String [] headPayload = token.split("\\.");
        //获得jwt解密后头部
        String header = decodedJWT.getHeader();
        //获得jwt解密后载荷
        String payload = decodedJWT.getPayload();
        if(!header.equals(headPayload[0]) && !payload.equals(headPayload[1])){
            throw new ResponseException(ErrorConstant.INTERNALSERVERERROR,"jwt解密出现错误,jwt或私钥或签证人不正确");
        }
        return new AESUtils(decodedJWT.getClaim("user").asString()).decrypt();
    }


}

