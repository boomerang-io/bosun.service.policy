package net.boomerangplatform.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measures {

	@JsonProperty("ncloc")
	private Integer ncloc;

	@JsonProperty("complexity")
	private Integer complexity;

	@JsonProperty("violations")
	private Integer violations;

	@JsonProperty("tests")
	private Integer tests;

	@JsonProperty("test_errors")
	private Integer testErrors;

	@JsonProperty("test_failures")
	private Integer testFailures;

	@JsonProperty("skipped_tests")
	private Integer skippedTests;

	@JsonProperty("test_success_density")
	private Double testSuccessDensity;

	@JsonProperty("test_execution_time")
	private Integer testExecutionTime;

	@JsonProperty("coverage")
	private Double coverage;

	@JsonProperty("lines_to_cover")
	private Integer linesToCover;

	@JsonProperty("uncovered_lines")
	private Integer uncoveredLines;

	@JsonProperty("line_coverage")
	private Double lineCoverage;

	public Measures() {
		// Do nothing
	}

	public Integer getNcloc() {
		return ncloc;
	}

	public void setNcloc(Integer ncloc) {
		this.ncloc = ncloc;
	}

	public Integer getComplexity() {
		return complexity;
	}

	public void setComplexity(Integer complexity) {
		this.complexity = complexity;
	}

	public Integer getViolations() {
		return violations;
	}

	public void setViolations(Integer violations) {
		this.violations = violations;
	}

	public Integer getTests() {
		return tests;
	}

	public void setTests(Integer tests) {
		this.tests = tests;
	}

	public Integer getTestErrors() {
		return testErrors;
	}

	public void setTestErrors(Integer testErrors) {
		this.testErrors = testErrors;
	}

	public Integer getTestFailures() {
		return testFailures;
	}

	public void setTestFailures(Integer testFailures) {
		this.testFailures = testFailures;
	}

	public Integer getSkippedTests() {
		return skippedTests;
	}

	public void setSkippedTests(Integer skippedTests) {
		this.skippedTests = skippedTests;
	}

	public Double getTestSuccessDensity() {
		return testSuccessDensity;
	}

	public void setTestSuccessDensity(Double testSuccessDensity) {
		this.testSuccessDensity = testSuccessDensity;
	}

	public Integer getTestExecutionTime() {
		return testExecutionTime;
	}

	public void setTestExecutionTime(Integer testExecutionTime) {
		this.testExecutionTime = testExecutionTime;
	}

	public Double getCoverage() {
		return coverage;
	}

	public void setCoverage(Double coverage) {
		this.coverage = coverage;
	}

	public Integer getLinesToCover() {
		return linesToCover;
	}

	public void setLinesToCover(Integer linesToCover) {
		this.linesToCover = linesToCover;
	}

	public Integer getUncoveredLines() {
		return uncoveredLines;
	}

	public void setUncoveredLines(Integer uncoveredLines) {
		this.uncoveredLines = uncoveredLines;
	}

	public Double getLineCoverage() {
		return lineCoverage;
	}

	public void setLineCoverage(Double lineCoverage) {
		this.lineCoverage = lineCoverage;
	}
}
