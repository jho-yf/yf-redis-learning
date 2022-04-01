package com.jho.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Author JHO
 * @Date 2021-05-10 23:32
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User implements Serializable {

    private String name;

    private Integer age;


}
