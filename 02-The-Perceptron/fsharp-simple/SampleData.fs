module SampleData

type sample = {inputs: float list; desired : int}

let repeatCount = 12;

let repeat list =
    [1..repeatCount]
    |> List.map (fun _ -> list)
    |> List.concat

let andSamples =     
    let core = [
            {inputs = [0.0; 0.0]; desired = -1}
            {inputs = [0.0; 1.0]; desired = -1}
            {inputs = [1.0; 0.0]; desired = -1}
            {inputs = [1.0; 1.0]; desired =  1}
        ]
    "And", (repeat core)

let nandSamples =     
    let core = [
            {inputs = [0.0; 0.0]; desired = 1}
            {inputs = [0.0; 1.0]; desired = 1}
            {inputs = [1.0; 0.0]; desired = 1}
            {inputs = [1.0; 1.0]; desired = -1}
        ]
    "Nand", (repeat core)

let orSamples =     
    let core = [
            {inputs = [0.0; 0.0]; desired = -1}
            {inputs = [0.0; 1.0]; desired = 1}
            {inputs = [1.0; 0.0]; desired = 1}
            {inputs = [1.0; 1.0]; desired = 1}
        ]
    "Or", (repeat core)

let notSamples =     
    let core = [
            {inputs = [0.0]; desired = 1}
            {inputs = [1.0]; desired = -1}
        ]
    "Not", (repeat core)

let xorSamples =     
    let core = [
            {inputs = [0.0; 0.0]; desired = -1}
            {inputs = [0.0; 1.0]; desired = 1}
            {inputs = [1.0; 0.0]; desired = 1}
            {inputs = [1.0; 1.0]; desired = -1}
        ]
    "Xor", (repeat core)

let allSamples = [
    andSamples
    nandSamples
    orSamples
    notSamples
    xorSamples
]




















