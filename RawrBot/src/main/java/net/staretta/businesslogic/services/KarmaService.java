package net.staretta.businesslogic.services;

import javax.transaction.Transactional;

import net.staretta.businesslogic.entity.KarmaEntity;
import net.staretta.modules.Karma;
import org.pircbotx.Channel;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class KarmaService extends BaseService
{
	public void modKarma(int count, String giver, String receiver, Channel channel)
	{
		KarmaEntity karma = new KarmaEntity();
		karma.setChannel(channel.getName());
		karma.setReceiver(receiver);
		karma.setGiver(giver);
		karma.setServer(channel.getBot().getServerInfo().getServerName());
		karma.setKarma(count);
		getSession().save(karma);
	}
}
