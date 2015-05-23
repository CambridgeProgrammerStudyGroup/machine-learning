module LogicGateCases

open Sample

let notCases = [| 
        createSample "0" "1";
        createSample "1" "0";
    |]

let orCases = [|
        createSample "0 0" "0";
        createSample "0 1" "1";
        createSample "1 0" "1";
        createSample "1 1" "1";
    |]

let andCases = [|
        createSample "0 0" "0";
        createSample "0 1" "0";
        createSample "1 0" "0";
        createSample "1 1" "1";
    |]

let nandCases = [|
        createSample "0 0" "1";
        createSample "0 1" "1";
        createSample "1 0" "1";
        createSample "1 1" "0";
    |]

let xorCases = [|
        createSample "0 0" "0";
        createSample "0 1" "1";
        createSample "1 0" "1";
        createSample "1 1" "0";
    |]
