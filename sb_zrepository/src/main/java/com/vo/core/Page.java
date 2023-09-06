package com.vo.core;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页查询的结果
 *
 * @author zhangzhen
 * @date 2023年9月6日
 *
 */
@Data
@AllArgsConstructor
public final class Page<T> {

	/**
	 * 每页的条数
	 */
	private final Integer size;

	/**
	 * 当前第几页
	 */
	private final Long page;

	/**
	 * 总页数
	 */
	private final Long totalPage;

	/**
	 * 总条数
	 */
	private final Long totalCount;

	/**
	 * 本页内容
	 */
	private final ImmutableList<T> list;

	/**
	 * 是否有下一页
	 *
	 * @return
	 *
	 */
	public boolean hasNextPage() {
		return this.page < this.totalPage;
	}
	public boolean hasPreviousPage() {
		return (this.page - 1) * this.size <= this.totalCount;
	}

	public boolean hasContent() {
		return this.getList().size() > 0;
	}
}
