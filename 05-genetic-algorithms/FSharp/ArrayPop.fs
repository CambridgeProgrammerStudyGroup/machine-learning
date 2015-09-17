module ArrayPop

open System

let random = new Random()

let getRandomSelection count (items:'a[])  =
    Array.init count (fun _ -> items.[random.Next(items.Length)])

let distance target peep =
    peep 
        |> Array.zip target
        |> Array.map (fun (a, b) -> if a = b then 1 else 0)
        |> Array.sum
        |> (fun sum -> float sum / float target.Length)

let crossover create fitness mutationRate first second =
    let xavier = create ()
    let firstPeeps, secondPeeps = fst first, fst second
    let mutationPerPeep = mutationRate / float (Array.length firstPeeps)
    let child =
        (firstPeeps, secondPeeps, xavier)
        |||> Array.map3  (fun f s m -> 
            if random.NextDouble() < mutationPerPeep then m
            else if random.NextDouble() < 0.5 then f else s )
        |> (fun c -> (c, fitness c))
    child