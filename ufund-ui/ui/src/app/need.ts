/**
 * Need object used for sending needs in HTTP requests and recieving them from HTTP responses
 */
export interface Need {
  id: number;
  name: string;
  description: string;
}