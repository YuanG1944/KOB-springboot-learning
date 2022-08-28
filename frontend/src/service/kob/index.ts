import request from "@/service/request";
import { addParamsObj } from "@/service/request";

const baseUrl = "http://127.0.0.1:3030";

const assembleUrl = (baseUrl: string, url: string) => {
  return baseUrl + url;
};

export const queryTest = (param: any): Promise<any> => {
  const url = "/pk/getbotinfo";
  return request(assembleUrl(baseUrl, url), {
    method: "GET",
  });
};
