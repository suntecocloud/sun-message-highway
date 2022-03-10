const minimist = require('minimist');
const fs = require('fs');
const readline = require('readline');

// cli config

const title = "Example Node.js";

const requiredOpts = [
  ['config', '--config CONFIG', 'The path to your Confluent Cloud configuration file'], 
  ['topic', '--topic TOPIC', 'The topic name on which to operate']
];

const requiredConfig = [
  ['bootstrap.servers', 'bootstrap.servers=<host1:port1...>', 'Your Confluent Cloud cluster bootstrap server(s). Separate multiple host/port pairs with commas.'],
  ['sasl.username', 'sasl.username=<string>', 'Your Confluent Cloud API key'],
  ['sasl.password', 'sasl.password=<string>', 'Your Confluent Cloud API secret'],
];

const alias = {
  t: 'topic',
  f: 'config'
};

exports.configFromCli = async function(args = process.argv.slice(2)) {
  const opts = minimist(args, { alias });
  const missingOpts = requiredOpts.filter(([k]) => !opts.hasOwnProperty(k));
  
  if (missingOpts.length) {
    return {
      ...opts,
      usage: usage('Some required arguments were not provided:', missingOpts)
    };
  }

  const config = await configFromPath(opts.config);
  const missingConfig = requiredConfig.filter(([k]) => !config.hasOwnProperty(k));

  if (missingConfig.length) {
    return {
      ...opts,
      ...config,
      usage: usage('Some required configuration values were not provided:', missingConfig)
    };
  }

  return { ...opts, ...config };
};

function usage(heading, missing) {
  const hints = missing.map(([,pattern,desc]) => `    ${pattern}
    ${desc}`);

      return `${title}

${heading}
${hints.join('\n\n')}
`;
}

// config file access and parsing

function readAllLines(path) {    
  return new Promise((resolve, reject) => {
    // Test file access directly, so that we can fail fast.
    // Otherwise, an ENOENT is thrown in the global scope by the readline internals.
    try {
      fs.accessSync(path, fs.constants.R_OK);
    } catch (err) {
      reject(err);
    }
    
    let lines = [];
    
    const reader = readline.createInterface({
      input: fs.createReadStream(path),
      crlfDelay: Infinity
    });
    
    reader
      .on('line', (line) => lines.push(line))
      .on('close', () => resolve(lines));
  });
}

async function configFromPath(path) {
  const lines = await readAllLines(path);

  return lines
    .filter((line) => !/^\s*?#/.test(line))
    .map((line) => line
      .split('=')
      .map((s) => s.trim()))
    .reduce((config, [k, v]) => {
      config[k] = v;
      return config;
    }, {});
};
