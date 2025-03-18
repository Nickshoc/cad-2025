package ru.bsuedu.cad.lab;

import org.springframework.stereotype.Component;

import java.util.List;

public interface Parser {
    List<Product> parse(String data);
}
