var INSTRUCTOR_ROLES = ['Instructor','TeachingAssistant','Teacher','Faculty'];
var LEARNER_ROLES = ['Student','Learner','Guest'];

var ContextMapping = function(json) {
    this.id = json.id;
    this.key = json.key;
    this.dashboards = json.dashboards;
    this.context = json.context;
    this.modified = json.modified;
};
ContextMapping.prototype = {};

var Course = function() {
	this.id = null;
	this.title = null;
	
	this.instructors = [];
	this.learners = [];
	
	this.events = [];
	this.events_median = null;
}
Course.prototype = {
	getId : function () {
		return this.id;
	},
	getTitle : function () {
		return this.title;
	},	
	getInstructors : function () {
		return this.instructors;
	},
	getLearners : function () {
		return this.learners;
	},
	getEvents : function () {
		return this.events;
	},
	fromLTI : function (lti_launch) {
		if (lti_launch) {
			this.id = lti_launch.context_id;
			
			var context_title = lti_launch.context_title;
            if (context_title) {
                this.title = lti_launch.context_title;
            }
            else {
                this.title = lti_launch.context_id;
            }
            
            var member = new Member();
            member.fromLTI(lti_launch);
            
            if (member.isInstructor()) {
            	this.instructors.push(member);
            }
            else {
            	this.learners.push(member);
            }
		}
	},
	buildRoster : function (members) {
		this.instructors = [];
		this.learners = [];
		_.forEach(members,function(member){
            if (member.isInstructor()) {
            	this.instructors.push(member);
            }
            else {
            	this.learners.push(member);
            }
		},this);
	}
};

var Person = function() {
	this.contact_email_primary = null;
	this.name_given = null;
	this.name_family = null;
	this.name_full = null;
	this.demographics = null;
};

Person.prototype = {};

var Demographics = function () {
	this.gender = null;
	this.date_of_birth = null;
	this.ethnicity = null;
	this.race = null;
	this.extension_map = null;
}
Demographics.prototype = {};


var Member = function() {
	this.user_id = null;
	this.user_image = null;
	this.role = null;
	this.roles = null;
	this.person = null;
	this.events = null;
	this.relative_activity_level = null;
	this.risk = null;
	this.last_activity = null;
};

Member.prototype = {
    getUserId : function () {
		return this.user_id;
	},
	fromLTI : function (lti_launch) {
		if (lti_launch) {
			this.user_id = lti_launch.user_id;
			this.user_image = lti_launch.user_image;
			this.roles = lti_launch.roles;
			
			this.person = new Person();
			this.person.contact_email_primary = lti_launch.lis_person_contact_email_primary;
			this.person.name_given = lti_launch.lis_person_name_given;
			this.person.name_family = lti_launch.lis_person_name_family;
			this.person.name_full = lti_launch.lis_person_name_full;
		}
	},
	fromLIS : function (LIS_member) {
		this.user_id = LIS_member.user_id;
		this.user_image = LIS_member.user_image;
		this.role = LIS_member.role;
		this.roles = LIS_member.roles;
		
		this.person = new Person();
		this.person.contact_email_primary = LIS_member.person_contact_email_primary;
		this.person.name_given = LIS_member.person_name_given;
		this.person.name_family = LIS_member.person_name_family;
		this.person.name_full = LIS_member.person_name_full;
	},
    isInstructor: function() {
        var isInstructor = false;
        
        if (this.roles) {
            var roleArray = null;
            if (this.roles.indexOf(',') > -1) {
                roleArray = this.roles.split(',');
            }
            else {
                roleArray = [this.roles];
            }
            
            var intersection = _.intersection(INSTRUCTOR_ROLES,roleArray);
            if (intersection && intersection.length > 0) {
                isInstructor = true;
            }
        }
        
        return isInstructor;
    },
    isStudent: function() {
        var isStudent = false;
        
        if (this.roles) {
            var roleArray = null;
            if (this.roles.indexOf(',') > -1) {
                roleArray = this.roles.split(',');
            }
            else {
                roleArray = [this.roles];
            }
            
            var intersection = _.intersection(LEARNER_ROLES,roleArray);
            if (intersection && intersection.length > 0) {
                isStudent = true;
            }
        }
        
        return isStudent;
    }
};

var Event = function() {
	var XAPI = 'XAPI';
	var CALIPER = 'CALIPER';
	
	this.user_id = null;
	this.context_id = null;
	this.organization_id = null;
	this.name_full = null;
	this.type = null;
	this.action = null;
	this.object = null;
	this.raw = null;
	this.timestamp = null;
};

Event.prototype = {
	fromXAPI: function(xapi) {
		this.type = this.XAPI;
		this.raw = xapi;
		this.timestamp = xapi.timestamp;
		
		if (xapi.actor) {
			var mbox = xapi.actor.mbox;
			if (mbox) {
				var uid = mbox;
				if (S(uid).contains(':')) {
					uid = uid.split(':')[1];
				}
				
				if (S(uid).contains('@')) {
					uid = uid.split('@')[0];
				}
				
				this.user_id = uid;
			}
			
			this.name_full = xapi.actor.name;
		}
		
		if (xapi.verb) {
			var xapiAction = xapi.verb.id;
			var lastSlash = xapiAction.lastIndexOf('/');
			this.action = xapiAction.substr(lastSlash + 1);
		}
		
		if (xapi.object) {
			var xapiObject = xapi.object.definition.type;
			var lastSlash = xapiObject.lastIndexOf('/');
			this.object = xapiObject.substr(lastSlash + 1);
		}
	}
};

