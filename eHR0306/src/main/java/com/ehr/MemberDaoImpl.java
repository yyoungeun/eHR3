package com.ehr;

import org.apache.log4j.Logger;

public class MemberDaoImpl implements CommonDao {
	
	private final Logger LOG = Logger.getLogger(MemberDaoImpl.class);

	@Override
	public void do_save() {
		LOG.debug("============================");
		LOG.debug("=do_save() 데이터 저장=");
		LOG.debug("============================");
	}

	@Override
	public void do_update() {
		LOG.debug("============================");
		LOG.debug("=do_update() 데이터 수정=");
		LOG.debug("============================");

	}

	@Override
	public void do_delete() {
		LOG.debug("============================");
		LOG.debug("=do_delete() 데이터 삭제=");
		LOG.debug("============================");
	}

	@Override
	public void do_retrieve() {
		LOG.debug("============================");
		LOG.debug("=do_retrieve() 데이터 조회=");
		LOG.debug("============================");

	}

	@Override
	public void selectOne() {
		LOG.debug("============================");
		LOG.debug("=selectOne() 데이터 단건조회=");
		LOG.debug("============================");

	}

}
