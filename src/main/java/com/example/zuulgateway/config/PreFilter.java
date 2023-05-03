package com.example.zuulgateway.config;

import com.example.zuulgateway.entity.AdminDetails;
import com.example.zuulgateway.repository.UserRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Service
public class PreFilter extends ZuulFilter {

    private final String secret = "foRmLeaveOutPass";

    @Autowired
    UserRepository userRepository;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        
        String requestBody = null;

        try {
            requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        }
        catch (Exception ignore){}
        System.out.println(requestBody);
        
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ZuulException("Invalid token", HttpStatus.UNAUTHORIZED.value(), "Token not found");
        }
        String token = header.substring(7);
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            String email = claims.getSubject();
            String body = "{\"createdBy\": "+email+" }";
//            ctx.setResponseBody(body);
//            ctx.setResponse(response);
            Optional<AdminDetails> optionalAdminDetails = userRepository.findByEmail(email);
            if (optionalAdminDetails.isEmpty()) {
                throw new ZuulException("Invalid token", HttpStatus.UNAUTHORIZED.value(), "User not found");
            }
            else{
                response.setHeader("userId", email);
                response.setHeader("Access-Control-Allow-Origin","*");
                response.setHeader("Access-Control-Allow-Methods", "DELETE, GET, OPTIONS, POST, PUT");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                ctx.addZuulRequestHeader("userId", email);
                ctx.addZuulResponseHeader("Access-Control-Allow-Origin","*");
            }

        } catch (Exception e) {
            ctx.setResponseBody("token is absent "+e.getMessage());
            ctx.setSendZuulResponse(false);
        }
        System.out.println("uri ==== "+request.getRequestURI());
        return null;
    }
}
