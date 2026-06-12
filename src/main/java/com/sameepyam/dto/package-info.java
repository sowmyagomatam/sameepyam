/**
 * Request/response records — the typed contract between controllers and the frontend (e.g.
 * {@code ScamVerdict}, {@code DocExplanation}). Spring AI maps model output onto these records via
 * {@code .call().entity(...)}.
 */
package com.sameepyam.dto;