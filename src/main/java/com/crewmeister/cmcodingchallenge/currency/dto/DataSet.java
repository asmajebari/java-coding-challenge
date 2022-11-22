
package com.crewmeister.cmcodingchallenge.currency.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "validFrom",
    "series",
    "links"
})
@Generated("jsonschema2pojo")
public class DataSet {

    @JsonProperty("action")
    private String action;
    @JsonProperty("validFrom")
    private String validFrom;
    @JsonProperty("series")
    private JsonNode series;
    @JsonProperty("links")
    private List<Object> links = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("validFrom")
    public Object getValidFrom() {
        return validFrom;
    }

    @JsonProperty("validFrom")
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    @JsonProperty("series")
    public JsonNode getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(JsonNode series) {
        this.series = series;
    }

    @JsonProperty("links")
    public List<Object> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Object> links) {
        this.links = links;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
