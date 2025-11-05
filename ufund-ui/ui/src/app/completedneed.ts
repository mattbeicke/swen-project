import { Need } from "./need";

/**
 * CompletedNeed object used for recieving data from HTTP responses
 */
export interface CompletedNeed {
  need: Need,
  contributor_name: string,
  timestamp: number
}