package com.vo.test;

import com.vo.ZEntityFragmentation;
import com.vo.ZID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ZEntity(tableName = "tea_1")
@ZEntityFragmentation(end = 10)
public class TeaEntity {

	@ZID
	private Integer id;

	private String name;

}
