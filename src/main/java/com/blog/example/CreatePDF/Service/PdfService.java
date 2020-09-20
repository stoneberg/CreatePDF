package com.blog.example.CreatePDF.Service;

import com.blog.example.CreatePDF.dto.UserInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setTitle("어서와 게이티는 처음이지?");
        userInfo.setFirstname("이");
        userInfo.setLastname("유평");
        userInfo.setStreet("Example Street 1001");
        userInfo.setZipCode("12345");
        userInfo.setCity("Annoy City");
        return userInfo;
    }


    public List<UserInfo> getUserInfoList() {
        List<UserInfo> userInfoList = new ArrayList<>();

        UserInfo userInfo = new UserInfo();
        userInfo.setTitle("어서와 게이티는 처음이지?");
        userInfo.setFirstname("박");
        userInfo.setLastname("주식");
        userInfo.setStreet("Example Street 1001");
        userInfo.setZipCode("11111");
        userInfo.setCity("Toll City");
        userInfoList.add(userInfo);

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setTitle("어서와 디에스는 처음이지?");
        userInfo2.setFirstname("이");
        userInfo2.setLastname("유평");
        userInfo2.setStreet("Example Street 1002");
        userInfo2.setZipCode("22222");
        userInfo2.setCity("Bang City");
        userInfoList.add(userInfo2);

        UserInfo userInfo3 = new UserInfo();
        userInfo3.setTitle("어서와 방배는 처음이지?");
        userInfo3.setFirstname("변");
        userInfo3.setLastname("종윤");
        userInfo3.setStreet("Example Street 1003");
        userInfo3.setZipCode("33333");
        userInfo3.setCity("Tune City");
        userInfoList.add(userInfo3);

        return userInfoList;

    }

}
