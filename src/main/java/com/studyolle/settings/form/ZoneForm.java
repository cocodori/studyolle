package com.studyolle.settings.form;

import com.studyolle.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {
    /*
    *   데이터는 'Seoul(서울)/None' 이런 형태로 들어온다.
    *   따라서 위 문자열에서 필요하지 않은 문자열을 제거하고
    *   cityName , localNameOfCity, province에 맞게 잘라서 사용한다.
    * */
    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));    //Seoul
    }

    public String getLocalNameOfCity() {
        //서울
        return zoneName.substring(zoneName.indexOf("(") +1, zoneName.indexOf(")"));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/")+1); //None
    }



    public Zone getZone() {
        return Zone.builder()
                .city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName())
                .build();
    }
}
