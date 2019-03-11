const chunk =
    (size, xs) =>
        xs.reduce(
            (segments, _, index) =>
                index % size === 0
                    ? [...segments, xs.slice(index, index + size)]
                    : segments,
            []
        );

export {chunk};