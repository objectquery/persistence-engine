package org.objectquery.persistence.engine.domain;

import java.time.LocalDate;

public interface FullTypeClass {

	String getName();

	void setName(String name);

	int getWeight();

	void setWeight(int weight);

	long getSoLong();

	void setSoLong(long soLong);

	LocalDate getBirthDate();

	void setBirthDate(LocalDate date);

	float getVote();

	void setVote(float vote);

	double getDetailedVote();

	void setDetailedVote(double vote);

	byte getFlags();

	void setFlags(byte flags);

	char getInitial();

	void setInitial(char initial);

	short getYesShort();

	void setYesShort(short yesS);

	byte[] getReallyRawData();

	void setReallyRawData(byte[] raw);

}
