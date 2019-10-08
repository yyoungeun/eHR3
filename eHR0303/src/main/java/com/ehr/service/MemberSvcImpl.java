package com.ehr.service;

import org.apache.log4j.Logger;

public class MemberSvcImpl implements MemberSvc {
	private final Logger LOG=Logger.getLogger(this.getClass());
	
	@Override
	public int do_save() {
		LOG.debug("===========================");
		LOG.debug("=do_save()=================");
		LOG.debug("===========================");
		return 0;
	}

	@Override
	public int do_update() {
		LOG.debug("===========================");
		LOG.debug("=do_update()=================");
		LOG.debug("===========================");
		return 0;
	}

	@Override
	public int delete() {
		LOG.debug("===========================");
		LOG.debug("=delete()=================");
		LOG.debug("===========================");
		return 0;
	}

}
