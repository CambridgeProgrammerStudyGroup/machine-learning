module Sample

open System.Text.RegularExpressions

type Sample = { Input : float[]; Target : float[] }

let getFloatsFromText text =
    let splitRx = new Regex(@"[\s,]+", RegexOptions.Compiled)
    splitRx.Split(text)
    |> Array.map (double)

let createSample inputText targetText =
  { Input = getFloatsFromText inputText
    Target = getFloatsFromText targetText }

