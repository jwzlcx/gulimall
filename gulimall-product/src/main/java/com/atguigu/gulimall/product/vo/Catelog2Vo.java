package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo implements Serializable {

	private String id;

	private String name;

	private String catalog1Id;//一级父分类id

	private List<Catalog3Vo> catalog3List;//三级子分类id
}
