package com.example.birt.engine.dto;

import java.util.List;

/**
 * Report DTO class
 */
public class Report {
    private String title;
    private String name;
    private List<Parameter> parameters;

    public Report(String title, String name) {
        this.title = title;
        this.name = name;
    }
    
    


    public Report() {
		super();
		// TODO Auto-generated constructor stub
	}




	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}




	public static class Parameter {
        private String title;
        private String name;
        private ParameterType type;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ParameterType getType() {
			return type;
		}
		public void setType(ParameterType type) {
			this.type = type;
		}
		public Parameter(String title, String name, ParameterType type) {
			super();
			this.title = title;
			this.name = name;
			this.type = type;
		}
		public Parameter() {
			super();
			// TODO Auto-generated constructor stub
		}
        
        

    }

    public enum ParameterType {
        INT, STRING
    }
}
