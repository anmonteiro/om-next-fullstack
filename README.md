# om-next-fullstack

Fullstack application demoing Om Next server-side rendering.

Note: Requires Om `1.0.0-alpha45`.

## Running

    boot dev

Then point your browser to `http://localhost:8081/`.

Largely adapted from https://github.com/swannodette/om-next-demo

## Oddities

### Reader Macros

`#js` reader macros will break when run server side with boot. `(boot.core/load-data-readers!)` is used in this repository's `build.boot` to properly load the data readers enclosed in om. Please do not forget to include this when building your own prerendered om.next app with boot!
