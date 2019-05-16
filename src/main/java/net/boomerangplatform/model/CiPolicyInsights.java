package net.boomerangplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.boomerangplatform.mongo.model.CiPolicyConfig;

public class CiPolicyInsights implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ciPolicyId;
	private String ciPolicyName;
	private Date createdDate;
	private String ciTeamId;
}
