package net.staretta.businesslogic.services;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RawrService extends BaseService
{
	public String getRandomRawr()
	{
		Query q = getSession().createQuery("SELECT r.message FROM RawrEntity r ORDER BY RANDOM()").setMaxResults(1);
		return q.uniqueResult().toString();
	}
}